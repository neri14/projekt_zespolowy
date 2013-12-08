using duta.Storage.Entities;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace duta.Interface
{
    public class UserDataResponse
    {
        public UserDataResponse(User usr)
        {
            user_id = usr.user_id;
            login = usr.login;
            status = (int)usr.status;
            description = usr.descripton;
        }

        public int user_id { get; set; }
        public string login { get; set; }
        public int status { get; set; }
        public string description { get; set; }
    }
}