using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Web.Mvc;
using Newtonsoft.Json;
using System.Threading.Tasks;
using System.Net;
using System.Collections.Generic;

namespace duta.Controllers
{
    [TestClass]
    public class ServiceControllerTest
    {
        private string Serialize(JsonResult result)
        {
            return JsonConvert.SerializeObject(result.Data);
        }

        [TestMethod]
        public void Service_LoginDummyUser()
        {
            ServiceController controller = new ServiceController();

            JsonResult result1 = controller.Login("user_a", "qwerty") as JsonResult;
            Assert.AreEqual("{\"logged_in\":1}", Serialize(result1));

            JsonResult result2 = controller.Login("user_b", "qwerty") as JsonResult;
            Assert.AreEqual("{\"logged_in\":0}", Serialize(result2));
        }

        [TestMethod]
        public void Service_GettingDummyContactList()
        {
            string expected = "[{\"user_id\":1," +
                              "\"login\":\"user_a\"," +
                              "\"nickname\":\"nick_a\"," +
                              "\"status\":1," +
                              "\"description\":\"desc_a\"}]";
            ServiceController controller = new ServiceController();

            JsonResult result = controller.GetContactList() as JsonResult;
            Assert.AreEqual(expected, Serialize(result));
        }

        [TestMethod]
        public void Service_GettingDummyStatusUpdate()
        {
            string expected = "[{\"user_id\":1," +
                              "\"status\":1," +
                              "\"description\":\"desc_a\"}," +
                              "{\"user_id\":2," +
                              "\"status\":0," +
                              "\"description\":\"desc_b\"}]";
            ServiceController controller = new ServiceController();

            Task<JsonResult> result = controller.GetStatusUpdate() as Task<JsonResult>;
            Assert.AreEqual(expected, Serialize(result.Result));
        }

        [TestMethod]
        public void Service_SetDummyStatus()
        {
            ServiceController controller = new ServiceController();

            HttpStatusCodeResult result1 = controller.SetStatus(1, "desc_a");
            Assert.AreEqual(200, result1.StatusCode);

            HttpStatusCodeResult result2 = controller.SetStatus(7, "desc_a");
            Assert.AreEqual(500, result2.StatusCode);
        }

        [TestMethod]
        public void Service_SendDummyMessage()
        {
            ServiceController controller = new ServiceController();

            List<int> users = new List<int>{ 1, 2, 3 };
            JsonResult result = controller.SendMessage(users, "msg") as JsonResult;

            Assert.IsNotNull(result);
        }

        [TestMethod]
        public void Service_GetMessage()
        {
            string expected = "[{\"users\":[1,2,3]," +
                              "\"timestamp\":1380629552000," +
                              "\"message\":\"msg\"}]";
            ServiceController controller = new ServiceController();

            Task<JsonResult> result = controller.GetMessage() as Task<JsonResult>;
            Assert.AreEqual(expected, Serialize(result.Result));
        }
    }
}
