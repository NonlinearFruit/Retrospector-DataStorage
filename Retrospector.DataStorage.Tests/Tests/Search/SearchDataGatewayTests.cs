using System;
using System.Collections.Generic;
using Retrospector.Core.Search.Interfaces;
using Retrospector.Core.Search.Models;
using Retrospector.DataStorage.Medias.Entities;
using Retrospector.DataStorage.Search;
using Retrospector.DataStorage.Tests.TestDoubles;
using Retrospector.DataStorage.Tests.TestDoubles.Search;
using Retrospector.DataStorage.Tests.Utilities;
using Xunit;

namespace Retrospector.DataStorage.Tests.Tests.Search
{
    public class SearchDataGatewayTests : IDisposable
    {
        private ISearchDataGateway _gateway;
        private DatabaseContext_TestDouble _arrangeContext;
        private DatabaseContext_TestDouble _actContext;
        private DatabaseContext_TestDouble _assertContext;
        private MediaReducer_TestDouble _mediaReducer;
        private ReviewReducer_TestDouble _reviewReducer;
        private FactoidReducer_TestDouble _factoidReducer;

        public SearchDataGatewayTests()
        {
            var id = Guid.NewGuid().ToString();
            _arrangeContext = new DatabaseContext_TestDouble(id);
            _actContext = new DatabaseContext_TestDouble(id);
            _assertContext = new DatabaseContext_TestDouble(id);
            _mediaReducer = new MediaReducer_TestDouble();
            _reviewReducer = new ReviewReducer_TestDouble();
            _factoidReducer = new FactoidReducer_TestDouble();
            _gateway = new SearchDataGateway(_mediaReducer, _reviewReducer, _factoidReducer, _actContext);
        }

        [Fact]
        public void empty_results_when_query_is_null()
        {
            var results = _gateway.Search(null);

            Assert.Empty(results);
        }

        [Fact]
        public void reduces_media_when_media_match_is_found()
        {
            var title = "Title";
            var id = _arrangeContext.Media.Add(new MediaEntity {Title = title}).Entity.Id;
            _arrangeContext.SaveChanges();
            var query = CreateQuery(RetrospectorAttribute.MediaTitle, title);

            _gateway.Search(query);

            Assert.Equal(id, _mediaReducer.LastItemPassedTo_Reduce.Id);
        }

        [Theory]
        [InlineData(0)]
        [InlineData(1)]
        [InlineData(10)]
        public void reduces_multiple_media_when_matches_are_found(int countOfMatches)
        {
            var title = "Title";
            for (var i = 0; i < countOfMatches; i++)
                _arrangeContext.Media.Add(new MediaEntity {Title = title});
            _arrangeContext.SaveChanges();
            var query = CreateQuery(RetrospectorAttribute.MediaTitle, title);

            _gateway.Search(query);

            Assert.Equal(countOfMatches, _mediaReducer.CountOf_Reduce_Calls);
        }

        private QueryTree CreateQuery(RetrospectorAttribute attribute, string value)
        {
            return new QueryTree
            {
                Type = OperatorType.And,
                Leaves = new[]
                {
                    new QueryLeaf
                    {
                        Attribute = attribute,
                        Comparator = Comparator.Equal,
                        SearchValue = value
                    }
                }
            };
        }

        public void Dispose()
        {
            _arrangeContext?.Dispose();
            _actContext?.Dispose();
            _assertContext?.Dispose();
        }
    }
}