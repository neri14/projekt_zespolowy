using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using duta.Debug;
using duta.ViewModels;
using System.Web.SessionState;

namespace duta.Controllers
{
    [SessionState(SessionStateBehavior.Disabled)]
    public class DebugController : Controller
    {
        //
        // GET: /Debug/Logs

        public ActionResult Logs(int id = 1)
        {
            DebugModel model = new DebugModel
            {
                Timeout = id,
                Logs = Logger.GetLogs()
            };

            return View(model);
        }
    }
}
