using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace duta.Debug.Event
{
    public class LoggerEvent
    {
        private DateTime time = DateTime.UtcNow;
        private string tag;
        private string msg;

        public LoggerEvent(string tag, string msg)
        {
            this.tag = tag;
            this.msg = msg;
        }

        public string What()
        {
            if (tag.Length > 8)
                tag = tag.Substring(0, 8);
            return String.Format("{0}   {1,8}   {2}", time.ToLongTimeString(), tag, msg);
        }
    }
}