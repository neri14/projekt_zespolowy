using System;
using System.Collections.Generic;
using System.Collections.Concurrent;
using System.Linq;
using System.Web;
using duta.Debug.Event;

namespace duta.Debug
{
    public static class Logger
    {
        private static ConcurrentQueue<LoggerEvent> queue = new ConcurrentQueue<LoggerEvent>();
        private static int LOG_LIMIT = 256;
        private static string DEFAULT_TAG = "DEFAULT";

        public static void Log(string msg)
        {
            TrimQueueTo(LOG_LIMIT - 1);
            queue.Enqueue(new LoggerEvent(DEFAULT_TAG, msg));
        }

        public static void Log(string tag, string msg)
        {
            TrimQueueTo(LOG_LIMIT - 1);
            queue.Enqueue(new LoggerEvent(tag, msg));
        }

        public static List<string> GetLogs()
        {
            List<string> result = new List<string>();
            foreach (LoggerEvent ev in queue.ToArray().Reverse())
            {
                result.Add(ev.What());
            }
            return result;
        }

        public static void SetLimit(int limit)
        {
            TrimQueueTo(limit);
            LOG_LIMIT = limit;
        }

        private static bool TrimQueueTo(int limit)
        {
            bool success = true;
            while (success && queue.Count > limit)
            {
                LoggerEvent old;
                success = queue.TryDequeue(out old);
            }
            return success;
        }
    }

    public class LoggerWrapper
    {
        private string tag;
        public LoggerWrapper(string tag)
        {
            this.tag = tag;
        }

        public void Log(string msg)
        {
            Logger.Log(tag, msg);
        }

        private string GenerateActionLog(string session, string action, string additional_msg)
        {
            return session + "    " + action + "    " + additional_msg;
        }

        public void LogActionEnter(string session, string action, string additional_msg = "") // add flag if action is entering or leaving and maybe request id, or smth like thread id?
        {
            Log(" IN    " + GenerateActionLog(session, action, additional_msg));
        }

        public void LogActionLeave(string session, string action, string additional_msg = "")
        {
            Log("OUT    " + GenerateActionLog(session, action, additional_msg));
        }
    }
}