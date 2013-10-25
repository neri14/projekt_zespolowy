using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace duta.Interface
{
    public class SendMessageResponse
    {
        public SendMessageResponse(long timestamp)
        {
            this.timestamp = timestamp;
        }

        public long timestamp { get; set; }
    }
}
