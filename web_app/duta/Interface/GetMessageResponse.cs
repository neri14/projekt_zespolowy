using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace duta.Interface
{
    public class GetMessageResponse_Message
    {
        public List<int> users { get; set; }
        public long timestamp { get; set; }
        public string message { get; set; }
    }
}