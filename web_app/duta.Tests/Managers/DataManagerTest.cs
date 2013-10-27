using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using duta.Managers;
using duta.Storage;
using System.Web.Security;

namespace duta.Tests.Managers
{
    [TestClass]
    public class DataManagerTest
    {
        private UserManager data;

/*        [TestInitialize]
        public void SetUp()
        {
            data = new UserManager(DataStorageCreator.create<InternalDataStorage>());
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
            int user_id = 1;

            int? user_id_1 = data.CreateUser(login, pass);
            Assert.AreEqual(user_id, user_id_1);

            Assert.AreEqual(user_id, data.GetUser(login).user_id);
            Assert.AreEqual(login, data.GetUser(user_id).login);

            int? user_id_2 = data.CreateUser(login, pass);
            Assert.AreEqual(null, user_id_2);
        }

        [TestMethod]
        public void DataManager_UserCanLogIn()
        {
            string login = "user_a";
            string pass1 = "qwerty";
            string pass2 = "ytrewq";
            data.CreateUser(login, pass1);

            Assert.IsTrue(data.Login(login, pass1));
            data.Logout();

            Assert.IsFalse(data.Login(login, pass2));
            data.Logout();
        }*/
    }
}
