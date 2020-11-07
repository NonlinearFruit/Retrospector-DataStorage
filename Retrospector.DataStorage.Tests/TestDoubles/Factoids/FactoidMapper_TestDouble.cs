using Retrospector.Core.Crud.Models;
using Retrospector.DataStorage.Factoids.Entities;
using Retrospector.DataStorage.Factoids.Interfaces;
using Retrospector.DataStorage.Tests.Utilities;

namespace Retrospector.DataStorage.Tests.TestDoubles.Factoids
{
    public class FactoidMapper_TestDouble : Mapper_TestDouble<Factoid, FactoidEntity>, IFactoidMapper
    { }
}