using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using duta.Managers;
using duta.Storage;

namespace duta.Tests.Managers
{
    [TestClass]
    public class DataManagerTest
    {
        private DataManager data;

        [TestInitialize]
        public void SetUp()
        {
            data = new DataManager(DataStorageCreator.create<InternalDataStorage>());
        }

        [TestCleanup]
        public void TearDown()
        {
            data = null;
        }

        [TestMethod]
        public void DataManager_CanCreateUserOnce()
        {
            string login = "user_a";
            string pass = "qwerty";

            int? user_id_1 = data.CreateUser(login, pass);
            Assert.AreEqual(1, user_id_1);

            Assert.AreEqual(login, data.GetUser().login);

            int? user_id_2 = data.CreateUser(login, pass);
            Assert.AreEqual(null, user_id_2);
        }

        [TestMethod]
        public void DataManager_CanDeleteUser()
        {
            //int? user_id = data.CreateUser(
        }
    }
}
