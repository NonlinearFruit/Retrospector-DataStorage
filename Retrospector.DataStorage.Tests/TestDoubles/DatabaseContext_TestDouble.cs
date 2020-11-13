using System;
using System.Diagnostics.CodeAnalysis;
using Microsoft.EntityFrameworkCore;
using Retrospector.DataStorage.Factoids.Entities;
using Retrospector.DataStorage.Medias.Entities;
using Retrospector.DataStorage.Reviews.Entities;

namespace Retrospector.DataStorage.Tests.TestDoubles
{
    [SuppressMessage("ReSharper", "InconsistentNaming")]
    public class DatabaseContext_TestDouble : DbContext, IDatabaseContext
    {
        private readonly string _id;

        public DatabaseContext_TestDouble(string id = null)
        {
            _id = id ?? Guid.NewGuid().ToString();
        }

        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
            optionsBuilder.UseInMemoryDatabase(_id);
        }

        public DbSet<FactoidEntity> Factoids { get; set; }
        public DbSet<ReviewEntity> Reviews { get; set; }
        public DbSet<MediaEntity> Media { get; set; }
    }
}