using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using duta.Interface;
using System.Threading.Tasks;
using System.Threading;
using System.Net;
using duta.Storage;
using duta.Managers;
using System.Web.Security;
using duta.Storage.Entities;

namespace duta.Controllers
{
    [Authorize]
    public class ServiceController : Controller
    {
/********************** DONE ****************************/
        [HttpPost]
        [AllowAnonymous]
        public JsonResult Login(string login, string password)
        {
            //bool resp = login == "user_a" && password == "qwerty";
            bool resp = UserManager.Login(login, password);
            Session["last_sent_status_update"] = new DateTime(1970, 1, 1);
            return Json(new LoginResponse(resp));
        }

        [HttpPost]
        public HttpStatusCodeResult Logout()
        {
            UserManager.Logout();
            return new HttpStatusCodeResult(HttpStatusCode.OK);
        }

        [HttpPost]
        public JsonResult GetContactList()
        {
            SortedSet<GetContactListResponse_User> list = new SortedSet<GetContactListResponse_User>();
            User user = UserManager.GetUser(System.Web.HttpContext.Current.User.Identity.Name);

            foreach(KeyValuePair<string, User> u in user.contact_list)
            {
                list.Add(new GetContactListResponse_User
                {
                    user_id = u.Value.user_id,
                    login = u.Value.login,
                    nickname = u.Key,
                    status = (int)u.Value.status,
                    description = u.Value.descripton
                });
            }
            return Json(list.OrderBy(u => u.user_id));
        }

        [HttpPost]
        [AllowAnonymous]
        public async Task<JsonResult> GetStatusUpdate()
        {
            List<User> updates = await UserManager.GetStatusUpdate(System.Web.HttpContext.Current.User.Identity.Name, (DateTime)Session["last_sent_status_update"]);
            Session["last_sent_status_update"] = DateTime.Now;

            List<GetStatusUpdateResponse_User> list = new List<GetStatusUpdateResponse_User>();
            foreach (User u in updates)
            {
                list.Add(new GetStatusUpdateResponse_User
                {
                    user_id = u.user_id,
                    status = (int)u.status,
                    description = u.descripton
                });
            }
            return Json(list.OrderBy(u => u.user_id));
        }

        [HttpPost]
        public HttpStatusCodeResult SetStatus(int status, string description)
        {
            EUserStatus eStatus;
            try
            {
                eStatus = (EUserStatus)status;
            }
            catch (Exception)
            {
                return new HttpStatusCodeResult(HttpStatusCode.InternalServerError);
            }

            UserManager.SetStatus(System.Web.HttpContext.Current.User.Identity.Name,
                eStatus, description);
            return new HttpStatusCodeResult(HttpStatusCode.OK);
        }

/********************** TODO ****************************/
        [HttpPost]
        public JsonResult SendMessage(List<int> users, string message)
        {
            //return 500 if fail - no user from users in storage e.g.

            TimeSpan t = DateTime.Now-new DateTime(1970, 1,1);
            return Json(new SendMessageResponse((long)t.TotalMilliseconds));
        }

        [HttpPost]
        public async Task<JsonResult> GetMessage()
        {
            List<GetMessageResponse_Message> msgs = new List<GetMessageResponse_Message>();

            TimeSpan t = new DateTime(2013, 10, 1, 12, 12, 32) - new DateTime(1970, 1, 1);
            long secondsSinceEpoch = (long)t.TotalMilliseconds;

            msgs.Add(new GetMessageResponse_Message
            {
                users = new SortedSet<int> { 1,2,3 },
                timestamp = (long)t.TotalMilliseconds,
                message = "msg"
            });

            return Json(msgs.OrderBy(m => m.timestamp));
        }
    }
}
