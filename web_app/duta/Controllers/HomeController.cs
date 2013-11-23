using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using duta.Debug;

namespace duta.Controllers
{
    public class HomeController : Controller
    {
        public ActionResult Index()
        {
            Logger.Log("HOME", "hello");
            ViewBag.Message = "Connecting people.";

            return View();
        }

        public ActionResult About()
        {
            ViewBag.Message = "Your app description page.";

            return View();
        }

        public ActionResult Contact()
        {
            ViewBag.Message = "Your contact page.";

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
