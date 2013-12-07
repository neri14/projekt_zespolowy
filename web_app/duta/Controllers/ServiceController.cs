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
using duta.Debug;
using System.Web.SessionState;

namespace duta.Controllers
{
    [Authorize]
    [SessionState(SessionStateBehavior.ReadOnly)]
    public class ServiceController : Controller
    {
        private LoggerWrapper logger = new LoggerWrapper("SRV_CTRL");

        [HttpPost]
        [AllowAnonymous]
        public JsonResult Register(string login, string password)
        {
            logger.LogActionEnter(Session.SessionID, "/Service/Register");

            int? uid = UserManager.CreateUser(login, password);

            RegisterResponse response = new RegisterResponse
            {
                registered = uid == null ? 0 : 1,
                user_id = uid == null ? 0 : uid.Value
            };
            logger.LogActionLeave(Session.SessionID, "/Service/Register");
            return Json(response);
        }

        [HttpPost]
        [AllowAnonymous]
        public JsonResult Login(string login, string password)
        {
            logger.LogActionEnter(Session.SessionID, "/Service/Login", "login: " + login);

            bool resp = UserManager.Login(login, password);
            int user_id = resp ? UserManager.GetUser(login).user_id : 0;

            Response.AppendCookie(new HttpCookie("last_sent_status_update", new DateTime(1970,1,1).ToBinary().ToString()));
            logger.LogActionLeave(Session.SessionID, "/Service/Login", (resp ? "" : "not ") + "logged in");
            return Json(new LoginResponse(resp, user_id));
        }

        [HttpPost]
        public HttpStatusCodeResult Logout()
        {
            logger.LogActionEnter(Session.SessionID, "/Service/Logout");

            UserManager.Logout();

            logger.LogActionLeave(Session.SessionID, "/Service/Logout");
            return new HttpStatusCodeResult(HttpStatusCode.OK);
        }

        [HttpPost]
        public JsonResult GetContactList()
        {
            logger.LogActionEnter(Session.SessionID, "/Service/GetContactList");

            SortedSet<GetContactListResponse_User> list = new SortedSet<GetContactListResponse_User>();
            User user = UserManager.GetUser(System.Web.HttpContext.Current.User.Identity.Name);

            Dictionary<string, User> contact_list = UserManager.GetContactList(user);
            foreach(KeyValuePair<string, User> u in contact_list)
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

            logger.LogActionLeave(Session.SessionID, "/Service/GetContactList", list.Count + " contacts sent");
            return Json(list.OrderBy(u => u.user_id));
        }

        [HttpPost]
        public async Task<JsonResult> GetStatusUpdate()
        {
            logger.LogActionEnter(Session.SessionID, "/Service/GetStatusUpdate");

            DateTime lastTime;
            try
            {
                lastTime = DateTime.FromBinary(long.Parse(Request.Cookies["last_sent_status_update"].Value));
            }
            catch (Exception e)
            {
                lastTime = new DateTime(1970, 1, 1);
            }

            List<User> updates = await UserManager.GetStatusUpdate(System.Web.HttpContext.Current.User.Identity.Name, lastTime);
            Response.AppendCookie(new HttpCookie("last_sent_status_update", DateTime.Now.ToBinary().ToString()));

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

            logger.LogActionLeave(Session.SessionID, "/Service/GetStatusUpdate", list.Count + " statuses sent");
            return Json(list.OrderBy(u => u.user_id));
        }

        [HttpPost]
        public HttpStatusCodeResult SetStatus(int status, string description)
        {
            logger.LogActionEnter(Session.SessionID, "/Service/SetStatus", "set status " + status + " and description: " + description);
            EUserStatus eStatus;
            try
            {
                eStatus = (EUserStatus)status;
            }
            catch (Exception)
            {
                logger.LogActionLeave(Session.SessionID, "/Service/SetStatus", "500");
                return new HttpStatusCodeResult(HttpStatusCode.InternalServerError);
            }

            UserManager.SetStatus(System.Web.HttpContext.Current.User.Identity.Name,
                eStatus, description);
            logger.LogActionLeave(Session.SessionID, "/Service/SetStatus", "200");
            return new HttpStatusCodeResult(HttpStatusCode.OK);
        }

        [HttpPost]
        public ActionResult SendMessage(List<int> users, string message)
        {
            logger.LogActionEnter(Session.SessionID, "/Service/SendMessage", users.Count + " users in conversation");
            try
            {
                User user = UserManager.GetUser(System.Web.HttpContext.Current.User.Identity.Name);
                DateTime sentTime = MessageManager.SendMessage(user.user_id, users, message);
                TimeSpan t = sentTime - new DateTime(1970, 1, 1);

                logger.LogActionLeave(Session.SessionID, "/Service/SendMessage", "200");
                return Json(new SendMessageResponse((long)t.TotalMilliseconds));
            }
            catch (Exception)
            {
                logger.LogActionLeave(Session.SessionID, "/Service/SendMessage", "500");
                return new HttpStatusCodeResult(HttpStatusCode.InternalServerError);
            }
        }

        [HttpPost]
        public async Task<JsonResult> GetMessage()
        {
            logger.LogActionEnter(Session.SessionID, "/Service/GetMessage");

            User usr = UserManager.GetUser(System.Web.HttpContext.Current.User.Identity.Name);
            List<Message> messages = await MessageManager.GetMessageUpdate(usr.user_id, MessageManager.GetLastMessageUpdate(usr.user_id));
            MessageManager.SetLastMessageUpdate(usr.user_id, DateTime.Now);

            List<GetMessageResponse_Message> response = new List<GetMessageResponse_Message>();

            foreach (Message msg in messages)
            {
                TimeSpan t = msg.time - new DateTime(1970, 1, 1);

                response.Add(new GetMessageResponse_Message
                {
                    users = msg.users.OrderBy(u => u).ToList(),
                    author = msg.author,
                    timestamp = (long)t.TotalMilliseconds,
                    message = msg.message
                });
            }

            logger.LogActionLeave(Session.SessionID, "/Service/GetMessage", response.Count + " messages");
            return Json(response.OrderBy(m => m.timestamp));
        }

        public JsonResult GetUserData(int user_id)
        {
            logger.LogActionEnter(Session.SessionID, "/Service/GetUserData", "by uid");
            User usr = UserManager.GetUser(user_id);
            logger.LogActionLeave(Session.SessionID, "/Service/GetUserData");
            return Json(new UserDataResponse(usr));
        }

        public JsonResult GetUserData(string login)
        {
            logger.LogActionEnter(Session.SessionID, "/Service/GetUserData", "by login");
            User usr = UserManager.GetUser(login);
            logger.LogActionLeave(Session.SessionID, "/Service/GetUserData");
            return Json(new UserDataResponse(usr));
        }
    }
}
