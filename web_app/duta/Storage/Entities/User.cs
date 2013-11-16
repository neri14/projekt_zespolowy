using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace duta.Storage.Entities
{
    public class User
    {
        public User(int user_id, string login, string password)
        {
            this.user_id = user_id;
            this.login = login;
            this.password = password;
            this.status = EUserStatus.EUserStatus_Offline;
            this.descripton = "";
            this.contact_list = new Dictionary<string, int>();
            this.last_status_update = DateTime.Now;
        }

        public int user_id { get; set; }
        public string login { get; set; }
        public string password { get; set; }
        public EUserStatus status { get; set; }
        public string descripton { get; set; }
        public Dictionary<string,int> contact_list { get; set; }
        public DateTime last_status_update { get; set; }
    }

    public enum EUserStatus
    {
        EUserStatus_OnLine,
        EUserStatus_Offline,
        EUserStatus_Away
    }
}