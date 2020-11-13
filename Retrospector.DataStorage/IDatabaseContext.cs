using Microsoft.EntityFrameworkCore;
using Retrospector.DataStorage.Factoids.Entities;
using Retrospector.DataStorage.Reviews.Entities;

namespace Retrospector.DataStorage
{
    public interface IDatabaseContext
    {
        DbSet<FactoidEntity> Factoids { get; set; }
        DbSet<ReviewEntity> Reviews { get; set; }
        int SaveChanges();
    }
}