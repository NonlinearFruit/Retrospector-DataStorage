using Retrospector.Core.Crud.Models;
using Retrospector.DataStorage.Medias.Entities;
using Retrospector.DataStorage.Medias.Interfaces;

namespace Retrospector.DataStorage.Medias
{
    public class MediaTypeMapper : IMediaTypeMapper
    {
        public MediaType ToModel(MediaTypeEntity entity)
        {
            return entity switch
            {
                MediaTypeEntity.Series => MediaType.Series,
                MediaTypeEntity.MiniSeries => MediaType.MiniSeries,
                MediaTypeEntity.Single => MediaType.Single,
                MediaTypeEntity.Wishlist => MediaType.Wishlist
            };
        }

        public MediaTypeEntity ToEntity(MediaType model)
        {
            return model switch
            {
                MediaType.Series => MediaTypeEntity.Series,
                MediaType.MiniSeries => MediaTypeEntity.MiniSeries,
                MediaType.Single => MediaTypeEntity.Single,
                MediaType.Wishlist => MediaTypeEntity.Wishlist
            };
        }
    }
}