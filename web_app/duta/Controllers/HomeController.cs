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
    }
}
