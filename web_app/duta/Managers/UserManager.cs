using duta.Storage;
using duta.Storage.Entities;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System.Web;
using System.Web.Security;

namespace duta.Managers
{
    public static class UserManager
    {
        private static DataStorage data = DataStorageCreator.Get();

        public static int? CreateUser(string login, string password)
        {
            if (data.GetUser(login) == null)
                return data.CreateUser(login, password);
            return null;
        }

        public static User GetUser(string login)
        {
            return data.GetUser(login);
        }

        public static User GetUser(int user_id)
        {
            return data.GetUser(user_id);
        }

        public static bool Login(string login, string password)
        {
            User u = data.GetUser(login);

            if (u != null && u.password == password)
            {
                FormsAuthentication.SetAuthCookie(login, true);
                return true;
            }
            return false;
        }

        public static void Logout()
        {
            FormsAuthentication.SignOut();
        }

        static Dictionary<string,TaskCompletionSource<bool>> awaitingStatusUpdates
            = new Dictionary<string,TaskCompletionSource<bool>>();
        public static async Task<List<User>> GetStatusUpdate(string login, DateTime lastUpdate)
        {
            lock(awaitingStatusUpdates)
            {
                awaitingStatusUpdates.Remove(login);
            }
            {
                List<User> changed = GetChangedStatuses(login, lastUpdate);
                if (changed.Count > 0)
                    return changed;
            }

            TaskCompletionSource<bool> task;
            lock(awaitingStatusUpdates)
            {
                task = new TaskCompletionSource<bool>();
                awaitingStatusUpdates.Add(login, task);
            }

            await task.Task;

            lock (awaitingStatusUpdates)
            {
                task = null;
                awaitingStatusUpdates.Remove(login);
            }
            return GetChangedStatuses(login, lastUpdate);
        }

        public static void SetStatus(string login, EUserStatus status, string description)
        {
            User u = GetUser(login);
            u.status = status;
            u.descripton = description;
            u.last_status_update = DateTime.Now;

            lock (awaitingStatusUpdates)
            {
                foreach (string notify in data.GetUsersWithLoginInContactList(login))
                {
                    if (awaitingStatusUpdates.ContainsKey(notify))
                    {
                        awaitingStatusUpdates.FirstOrDefault(p => p.Key == notify).Value.SetResult(true);
                    }
                }
            }
        }

        public static Dictionary<string, User> GetContactList(User user)
        {
            Dictionary<string, User> contact_list = new Dictionary<string, User>();
            foreach (KeyValuePair<string, int> pair in user.contact_list)
            {
                contact_list[pair.Key] = GetUser(pair.Value);
            }
            return contact_list;
        }

        private static List<User> GetChangedStatuses(string login, DateTime lastUpdate)
        {
            User u = data.GetUser(login);

            List<User> changed = new List<User>();
            foreach (KeyValuePair<string, int> pair in u.contact_list)
            {
                User contact = data.GetUser(pair.Value);
                if (contact.last_status_update > lastUpdate)
                {
                    changed.Add(contact);
                }
            }
            //List<User> changed = u.contact_list.Values.Where(c => c.last_status_update > lastUpdate).ToList();
            return changed;
        }
    }
}