using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace duta.ViewModels
{
    public class DebugModel
    {
        public int Timeout { get; set; }
        public List<string> Logs { get; set; }
    }
}