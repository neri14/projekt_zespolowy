using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using duta.Interface;
using System.Threading.Tasks;
using System.Threading;
using System.Net;

namespace duta.Controllers
{
    [Authorize]
    public class ServiceController : Controller
    {
        //
        // GET: /Service/

        public ActionResult Index()
        {
            return View();
        }

//~/Service/Login                    Client -> Server : {login : string, password : string}
//                                   Server <- Client : {logged_in : int}                          //int as bool 0,1
        [HttpPost]
        [AllowAnonymous]
        public JsonResult Login(string login, string password)
        {
            bool resp = login == "user_a" && password == "qwerty";

            return Json(new LoginResponse(resp));
        }

//~/Service/GetContactList           Client -> Server : {}
//                                   Server -> Client : { [{user_id, login, nickname : string, status, description}] }
        [HttpPost]
        [AllowAnonymous]
        public JsonResult GetContactList()
        {
            SortedSet<GetContactListResponse_User> list = new SortedSet<GetContactListResponse_User>();
            list.Add(new GetContactListResponse_User
            {
                user_id = 1,
                login = "user_a",
                nickname = "nick_a",
                status = 1,
                description = "desc_a"
            });
            return Json(list);
        }

//~/Service/GetStatusUpdate          Client -> Server : {}
//                                   Server -> Client : { [{user_id, status, description}] }     //long polling on change of status/desc from friendlist
        [HttpPost]
        [AllowAnonymous]
        public async Task<JsonResult> GetStatusUpdate()
        {
            SortedSet<GetStatusUpdateResponse_User> list = new SortedSet<GetStatusUpdateResponse_User>();
            list.Add(new GetStatusUpdateResponse_User
            {
                user_id = 1,
                status = 1,
                description = "desc_a"
            });
            list.Add(new GetStatusUpdateResponse_User
            {
                user_id = 2,
                status = 0,
                description = "desc_b"
            });
            return Json(list);
        }

//~/Service/SetStatus                Client -> Server : {status, description}
//                                   Server -> Client : 200/500   return new HttpStatusCodeResult(HttpStatusCode.OK);
        [HttpPost]
        [AllowAnonymous]
        public HttpStatusCodeResult SetStatus(int status, string description)
        {
            if (status > 4) //check if enum is in range
            {
                return new HttpStatusCodeResult(HttpStatusCode.InternalServerError);
            }
            else
            {
                return new HttpStatusCodeResult(HttpStatusCode.OK);
            }
        }

//~/Service/SendMessage              Client -> Server : { users : [{ user_id }], message : string }                  //users is sorted
//                                   Server -> Client : 200/500
        [HttpPost]
        [AllowAnonymous]
        public JsonResult SendMessage(List<int> users, string message)
        {
            //return 500 if fail - no user from users in storage e.g.

            TimeSpan t = DateTime.Now-new DateTime(1970, 1,1);
            return Json(new SendMessageResponse((long)t.TotalMilliseconds));
        }

//~/Service/GetMessage               Client -> Server : {}
//                                   Server -> Client : { [ { users : [{ user_id }], timestamp : datetime, message : string } ] }   //datetime milliseconds from epoch  1.1.1970
        [HttpPost]
        [AllowAnonymous]
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
