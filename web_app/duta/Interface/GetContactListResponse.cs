using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace duta.Interface
{
    public class GetContactListResponse_User : IComparable
    {
        public int user_id { get; set; }
        public string login { get; set; }
        public string nickname { get; set; }
        public int status { get; set; }
        public string description { get; set; }

        public int CompareTo(object obj)
        {
            if (obj == null)
            {
                return 1;
            }

            GetContactListResponse_User user = obj as GetContactListResponse_User;

            if (user == null)
            {
                throw new ArgumentException(this.ToString());
            }
            else
            {
                return this.user_id.CompareTo(user.user_id);
            }
        }
    }
}