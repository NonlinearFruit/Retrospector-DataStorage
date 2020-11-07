using Microsoft.EntityFrameworkCore;
using Retrospector.DataStorage.Factoids.Entities;

namespace Retrospector.DataStorage
{
    public class DatabaseContext : DbContext, IDatabaseContext
    {
        private static DbContextOptions<DatabaseContext> GetOptions(DatabaseConfiguration config)
        {
            var options = new DbContextOptionsBuilder<DatabaseContext>();
            options.UseSqlServer(config.ConnectionString);
            return options.Options;
        }

        public DatabaseContext(DatabaseConfiguration config) : base(GetOptions(config))
        {}

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
        }

        public DbSet<FactoidEntity> Factoids { get; set; }
    }
}