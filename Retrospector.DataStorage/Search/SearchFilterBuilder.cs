using System.Collections.Generic;
using System.Linq;
using System.Linq.Expressions;
using Retrospector.Core.Search.Models;
using Retrospector.DataStorage.Factoids.Entities;
using Retrospector.DataStorage.Medias.Entities;
using Retrospector.DataStorage.Reviews.Entities;
using Retrospector.DataStorage.Search.Interfaces;
using Filter = System.Func<Retrospector.DataStorage.Medias.Entities.MediaEntity, Retrospector.DataStorage.Reviews.Entities.ReviewEntity, Retrospector.DataStorage.Factoids.Entities.FactoidEntity, bool>;

namespace Retrospector.DataStorage.Search
{
    public class SearchFilterBuilder : ISearchFilterBuilder
    {
        private readonly ILeafExpressionBuilder _leafBuilder;
        private ParameterExpression _mediaParameter = Expression.Parameter(typeof(MediaEntity), "media");
        private ParameterExpression _reviewParameter = Expression.Parameter(typeof(ReviewEntity), "review");
        private ParameterExpression _factoidParameter = Expression.Parameter(typeof(FactoidEntity), "factoid");
        private static Filter MatchEverything = (m, r, f) => true;
        private static Expression<Filter> MatchEverything2 = (m, r, f) => true;

        public SearchFilterBuilder(ILeafExpressionBuilder leafBuilder)
        {
            _leafBuilder = leafBuilder;
        }

        public Filter BuildFilter(QueryTree tree)
        {
            if (tree == null || tree.Type == OperatorType.GiveMeEverything || tree.Type == OperatorType.Leaf)
                return MatchEverything;
            var expression = ParseTree(tree);
            return Compile(expression.First());
        }

        private Filter Compile(Expression expression)
        {
            return Expression.Lambda<Filter>(
                expression,
                _mediaParameter, _reviewParameter, _factoidParameter
            ).Compile();
        }

        private InvocationExpression Invoke(Expression<Filter> func)
        {
            return Expression.Invoke(func, _mediaParameter, _reviewParameter, _factoidParameter);
        }

        private IEnumerable<Expression> ParseTree(QueryTree tree)
        {
            if (tree.Type == OperatorType.GiveMeEverything)
                return new []{MatchEverything2};
            if (tree.Type == OperatorType.Leaf)
                return tree.Leaves.Select(l => (Expression) Invoke(_leafBuilder.BuildExpression(l)));
            var expressions = tree
                .Branches
                .SelectMany(ParseTree)
                .DefaultIfEmpty(Invoke(MatchEverything2));
            return new [] {tree.Type switch
            {
                OperatorType.Not => Expression.Not(expressions.First()),
                OperatorType.And => expressions.Aggregate(Expression.And),
                OperatorType.Or => expressions.Aggregate(Expression.Or)
            }};
        }
    }
}