using Microsoft.EntityFrameworkCore;
using Retrospector.DataStorage.Factoids.Entities;
using Retrospector.DataStorage.Medias.Entities;
using Retrospector.DataStorage.Reviews.Entities;

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
        public DbSet<ReviewEntity> Reviews { get; set; }
        public DbSet<MediaEntity> Media { get; set; }
    }
}