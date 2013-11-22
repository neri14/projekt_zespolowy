using duta.Storage;
using duta.Storage.Entities;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System.Web;

namespace duta.Managers
{
    public static class MessageManager
    {
        private static DataStorage data = DataStorageCreator.Get();

        private static Dictionary<int, TaskCompletionSource<bool>> awaitingMessageUpdates
            = new Dictionary<int, TaskCompletionSource<bool>>();

        public static async Task<List<Message>> GetMessageUpdate(int user, DateTime lastUpdate)
        {
            lock (awaitingMessageUpdates)
            {
                awaitingMessageUpdates.Remove(user);
            }
            {
                List<Message> changed = data.GetMessagesSince(user, lastUpdate);
                if(changed.Count > 0)
                    return changed;
            }

            TaskCompletionSource<bool> task;
            lock(awaitingMessageUpdates)
            {
                task = new TaskCompletionSource<bool>();
                awaitingMessageUpdates.Add(user, task);
            }

            await task.Task;

            lock (awaitingMessageUpdates)
            {
                task = null;
                awaitingMessageUpdates.Remove(user);
            }
            return data.GetMessagesSince(user, lastUpdate);
        }

        public static DateTime SendMessage(int sender, List<int> receivers, string message)
        {
            DateTime time = DateTime.Now;
            data.AddMessage(time, receivers, sender, message);

            lock (awaitingMessageUpdates)
            {
                foreach (int u in receivers.Where(r => r != sender))
                {
                    if (awaitingMessageUpdates.ContainsKey(u))
                    {
                        awaitingMessageUpdates.FirstOrDefault(p => p.Key == u).Value.SetResult(true);
                    }
                }
            }
            return time;
        }

        public static DateTime GetLastMessageUpdate(int user_id)
        {
            return data.GetLastMessageUpdate(user_id);
        }

        public static void SetLastMessageUpdate(int user_id, DateTime time)
        {
            data.SetLastMessageUpdate(user_id, time);
        }
    }
}