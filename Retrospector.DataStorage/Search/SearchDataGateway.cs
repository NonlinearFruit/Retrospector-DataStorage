using System.Collections.Generic;
using System.Linq;
using Retrospector.Core.Search.Interfaces;
using Retrospector.Core.Search.Models;
using Retrospector.DataStorage.Search.Interfaces;

namespace Retrospector.DataStorage.Search
{
    public class SearchDataGateway : ISearchDataGateway
    {
        private readonly IMediaReducer _mediaReducer;
        private readonly IReviewReducer _reviewReducer;
        private readonly IFactoidReducer _factoidReducer;
        private readonly IDatabaseContext _context;

        public SearchDataGateway(
            IMediaReducer mediaReducer,
            IReviewReducer reviewReducer,
            IFactoidReducer factoidReducer,
            IDatabaseContext context)
        {
            _mediaReducer = mediaReducer;
            _reviewReducer = reviewReducer;
            _factoidReducer = factoidReducer;
            _context = context;
        }

        public IEnumerable<Dictionary<RetrospectorAttribute, string>> Search(QueryTree query)
        {
            _context.Media.Select(_mediaReducer.Reduce).ToList();
            return new Dictionary<RetrospectorAttribute, string>[0];
        }
    }
}