using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace duta.Storage.Entities
{
    public class Message
    {
        public Message(long message_id, DateTime time, List<int> users, string message)
        {
            this.message_id = message_id;
            this.time = time;
            this.users = users;
            this.message = message;
        }

        public long message_id { get; set; }
        public DateTime time { get; set; }
        public List<int> users { get; set; }
        public string message { get; set; }
    }
}