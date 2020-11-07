using Microsoft.EntityFrameworkCore;
using Retrospector.DataStorage.Factoids.Entities;

namespace Retrospector.DataStorage
{
    public interface IDatabaseContext
    {
        DbSet<FactoidEntity> Factoids { get; set; }
        int SaveChanges();
    }
}