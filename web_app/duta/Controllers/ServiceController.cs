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

            bool resp = UserManager.Login(login, password, Session.SessionID);
            int user_id = resp ? UserManager.GetUser(login).user_id : 0;

            Response.AppendCookie(new HttpCookie("last_sent_status_update", new DateTime(1970,1,1).ToBinary().ToString()));
            logger.LogActionLeave(Session.SessionID, "/Service/Login", (resp ? "" : "not ") + "logged in");
            return Json(new LoginResponse(resp, user_id));
        }

        [HttpPost]
        public HttpStatusCodeResult Logout()
        {
            logger.LogActionEnter(Session.SessionID, "/Service/Logout");

            UserManager.Logout(System.Web.HttpContext.Current.User.Identity.Name);

            logger.LogActionLeave(Session.SessionID, "/Service/Logout");
            return new HttpStatusCodeResult(HttpStatusCode.OK);
        }

        [HttpPost]
        public ActionResult GetContactList()
        {
            if (!PingNotif())
            {
                return new HttpStatusCodeResult(HttpStatusCode.Unauthorized);
            }

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

        public ActionResult AddContact(string login, string nickname)
        {
            if (UserManager.AddContact(System.Web.HttpContext.Current.User.Identity.Name, login, nickname))
                return new HttpStatusCodeResult(HttpStatusCode.OK);
            else
                return new HttpStatusCodeResult(HttpStatusCode.InternalServerError);
        }

        public ActionResult RemoveContact(string login)
        {
            if (UserManager.RemoveContact(System.Web.HttpContext.Current.User.Identity.Name, login))
                return new HttpStatusCodeResult(HttpStatusCode.OK);
            else
                return new HttpStatusCodeResult(HttpStatusCode.InternalServerError);
        }

        public ActionResult UpdateContact(string login, string nickname)
        {
            if (UserManager.UpdateContact(System.Web.HttpContext.Current.User.Identity.Name, login, nickname))
                return new HttpStatusCodeResult(HttpStatusCode.OK);
            else
                return new HttpStatusCodeResult(HttpStatusCode.InternalServerError);
        }

        [HttpPost]
        public async Task<ActionResult> GetStatusUpdate()
        {
            if (!PingNotif())
            {
                return new HttpStatusCodeResult(HttpStatusCode.Unauthorized);
            }

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

        //[HttpPost]
        [HttpGet]
        public ActionResult SetStatus(int status, string description)
        {
            if (!PingNotif())
            {
                return new HttpStatusCodeResult(HttpStatusCode.Unauthorized);
            }

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
            if (!PingNotif())
            {
                return new HttpStatusCodeResult(HttpStatusCode.Unauthorized);
            }

            logger.LogActionEnter(Session.SessionID, "/Service/SendMessage", users.Count + " users in conversation");
            try
            {
                User user = UserManager.GetUser(System.Web.HttpContext.Current.User.Identity.Name);
                DateTime sentTime = MessageManager.SendMessage(user.user_id, users, message);
                TimeSpan t = sentTime - new DateTime(1970, 1, 1);

                logger.LogActionLeave(Session.SessionID, "/Service/SendMessage", "200");
                return Json(new SendMessageResponse((long)t.TotalMilliseconds));
            }
            catch (Exception e)
            {
                logger.LogActionLeave(Session.SessionID, "/Service/SendMessage", "500");

                Exception ex = e;
                while (ex != null)
                {
                    logger.Log(ex.Message);
                    ex = ex.InnerException;
                }
                return new HttpStatusCodeResult(HttpStatusCode.InternalServerError);
            }
        }

        [HttpPost]
        public async Task<ActionResult> GetMessage()
        {
            if (!PingNotif())
            {
                return new HttpStatusCodeResult(HttpStatusCode.Unauthorized);
            }

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

        [HttpPost]
        public ActionResult GetUserDataById(int user_id)
        {
            if (!PingNotif())
            {
                return new HttpStatusCodeResult(HttpStatusCode.Unauthorized);
            }

            logger.LogActionEnter(Session.SessionID, "/Service/GetUserData", "by uid");
            User usr = UserManager.GetUser(user_id);
            logger.LogActionLeave(Session.SessionID, "/Service/GetUserData");
            return Json(new UserDataResponse(usr));
        }

        [HttpPost]
        public ActionResult GetUserDataByLogin(string login)
        {
            if (!PingNotif())
            {
                return new HttpStatusCodeResult(HttpStatusCode.Unauthorized);
            }

            logger.LogActionEnter(Session.SessionID, "/Service/GetUserData", "by login");
            User usr = UserManager.GetUser(login);
            logger.LogActionLeave(Session.SessionID, "/Service/GetUserData");
            return Json(new UserDataResponse(usr));
        }

        [HttpPost]
        public ActionResult GetMyData()
        {
            if (!PingNotif())
            {
                return new HttpStatusCodeResult(HttpStatusCode.Unauthorized);
            }

            logger.LogActionEnter(Session.SessionID, "/Service/GetUserData", "by login");
            User usr = UserManager.GetUser(System.Web.HttpContext.Current.User.Identity.Name);
            logger.LogActionLeave(Session.SessionID, "/Service/GetUserData");
            return Json(new UserDataResponse(usr));
        }

        [HttpPost]
        public HttpStatusCodeResult Ping()
        {
            logger.LogActionEnter(Session.SessionID, "/Service/Ping");
            if (!PingNotif())
            {
                logger.LogActionLeave(Session.SessionID, "/Service/Ping", "failure");
                return new HttpStatusCodeResult(HttpStatusCode.Unauthorized);
            }

            logger.LogActionLeave(Session.SessionID, "/Service/Ping", "success");
            return new HttpStatusCodeResult(HttpStatusCode.OK);
        }

        [HttpPost]
        public async Task<HttpStatusCodeResult> PingAsync()
        {
            if (!PingNotif())
            {
                return new HttpStatusCodeResult(HttpStatusCode.Unauthorized);
            }

            await Task.Delay(60 * 1000);
            return new HttpStatusCodeResult(HttpStatusCode.OK);
        }

        [HttpPost]
        public ActionResult GetArchive(DateTime from, DateTime to)
        {
            List<string> usernames = new List<string>();
            usernames.Add(System.Web.HttpContext.Current.User.Identity.Name);
            return Json(GenerateArchiveResponse(MessageManager.GetArchive(from, to, usernames)));
        }

        [HttpPost]
        public ActionResult GetArchiveFilteredByUserName(long from, long to, string username)
        {
            List<string> usernames = new List<string>();
            usernames.Add(System.Web.HttpContext.Current.User.Identity.Name);
            usernames.Add(username);
            return Json(GenerateArchiveResponse(MessageManager.GetArchive(TimeStampToDateTime(from), TimeStampToDateTime(to), usernames)));
        }

        [HttpPost]
        public ActionResult GetArchiveFilteredByUserId(long from, long to, int userid)
        {
            List<string> usernames = new List<string>();
            usernames.Add(System.Web.HttpContext.Current.User.Identity.Name);
            usernames.Add(UserManager.GetUser(userid).login);
            return Json(GenerateArchiveResponse(MessageManager.GetArchive(TimeStampToDateTime(from), TimeStampToDateTime(to), usernames)));
        }

        private List<GetArchiveResponse_Message> GenerateArchiveResponse(List<Message> msgs)
        {
            List<GetArchiveResponse_Message> response = new List<GetArchiveResponse_Message>();
            foreach (Message msg in msgs)
            {
                TimeSpan t = msg.time - new DateTime(1970, 1, 1);

                response.Add(new GetArchiveResponse_Message
                {
                    users = msg.users.OrderBy(u => u).ToList(),
                    author = msg.author,
                    timestamp = (long)t.TotalMilliseconds,
                    message = msg.message
                });
            }
            return response;
        }

        private bool PingNotif()
        {
            if (!UserManager.Ping(System.Web.HttpContext.Current.User.Identity.Name, Session.SessionID))
            {
                UserManager.Logout(System.Web.HttpContext.Current.User.Identity.Name);
                return false;
            }
            return true;
        }

        private DateTime TimeStampToDateTime(long timestamp)
        {
            DateTime dt = new DateTime(1970,1,1,0,0,0,0);
            dt = dt.AddMilliseconds(timestamp);
            return dt;
        }
    }
}
