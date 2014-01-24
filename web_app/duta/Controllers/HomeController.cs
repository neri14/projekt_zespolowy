using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using duta.Debug;
using System.Web.SessionState;

namespace duta.Controllers
{
    [SessionState(SessionStateBehavior.ReadOnly)]
    public class HomeController : Controller
    {
        public ActionResult Index()
        {
            return View();
        }

        public ActionResult Register()
        {
            return View();
        }

        public ActionResult komunikator()
        {
            ViewBag.Message = "Duta";
            ViewBag.Login = System.Web.HttpContext.Current.User.Identity.Name;

            return View();
        }

        public ActionResult contactView()
        {
            return PartialView();
        }

        public ActionResult tabListItemView()
        {
            return PartialView();
        }
        public ActionResult messagesPanelView()
        {
            return PartialView();
        }
        public ActionResult conversationView()
        {
            return PartialView();
        }

        public ActionResult messageView()
        {
            return PartialView();
        }

        public ActionResult archiveView()
        {
            return PartialView();
        }

        public ActionResult userArchiveView()
        {
            return PartialView();
        }

        public ActionResult addContactView()
        {
            return PartialView();
        }

        public ActionResult editContactView()
        {
            return PartialView();
        }

        public ActionResult conferenceView()
        {
            return PartialView();
        }
    }
}
