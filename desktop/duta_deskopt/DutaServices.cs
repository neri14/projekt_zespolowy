using System;
using System.Collections.Generic;
using System.Collections.Specialized;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;
using System.Web.Script.Serialization;
using System.IO;
using System.Threading;
using System.Net.Http.Headers;

namespace duta_deskopt
{
    [Serializable]
    public struct LoginResult
    {
        public int logged_in { get; set; }
        public int user_id { get; set; }
    }

    [Serializable]
    public struct GetContactListResult
    {
        public int user_id { get; set; }
        public string login { get; set; }
        public string nickname { get; set; }
        public int status { get; set; }
        public string description { get; set; }
    }

    [Serializable]
    public struct GetMessageResponse
    {
        public List<int> users { get; set; }
        public int author { get; set; }
        public long timestamp { get; set; }
        public string message { get; set; }
    }

    public struct SendMessangeResponse
    {
        public long timestamp { get; set; }
    }

    public struct UserDataResponse
    {
        public int user_id { get; set; }
        public string login { get; set; }
        public int status { get; set; }
        public string description { get; set; }
    }

    public struct GetStatusUpdateResponse_User
    {
        public int user_id { get; set; }
        public int status { get; set; }
        public string description { get; set; }
    }

    

    public class RequestState
    {
        const int BufferSize = 1024;
        public StringBuilder RequestData;
        public byte[] BufferRead;
        public WebRequest Request;
        public Stream ResponseStream;
        public Decoder StreamDecode = Encoding.UTF8.GetDecoder();

        public RequestState()
        {
            BufferRead = new byte[BufferSize];
            RequestData = new StringBuilder(String.Empty);
            Request = null;
            ResponseStream = null;
        }
    }



    public class DutaServices
    {
        //private String homeUrl = "http://localhost:1404";
        private String homeUrl = "http://duta.hostingasp.pl";
        private String cookieUrl = "duta.hostingasp.pl";
        private String auth;
        private String session;

        public static ManualResetEvent allDone = new ManualResetEvent(false);
        const int BUFFER_SIZE = 1024;

        private MainWindow mw;

        public DutaServices(MainWindow mw)
        {
            this.mw = mw;
        }

        public LoginResult Login(String login, String password)
        {

            string postData = "login="+login;
            postData += "&password=" + password;
            byte[] data = System.Text.Encoding.UTF8.GetBytes(postData);

            var http = (HttpWebRequest)WebRequest.Create(homeUrl + "/Service/Login");
            http.Method = "POST";
            http.ContentType = "application/x-www-form-urlencoded";

            using (var writer = new StreamWriter(http.GetRequestStream()))
            {
                writer.Write("login="+login);
                writer.Write("&password=" + password);
            }
            

            HttpWebResponse response = (HttpWebResponse)http.GetResponse();
            String header = response.Headers.Get("Set-Cookie");
            String[] headerArray = header.Split(';');
            auth = headerArray[2].Split('=')[1];
            session = headerArray[0].Split('=')[1];

            StreamReader loResponseStream = new StreamReader(response.GetResponseStream());
            string responsebody = loResponseStream.ReadToEnd();
            JavaScriptSerializer json_serializer = new JavaScriptSerializer();
            LoginResult loginResult = json_serializer.Deserialize<LoginResult>(responsebody);
            
            return loginResult;
        }

        public void Logout() {
            var http = (HttpWebRequest)WebRequest.Create(homeUrl + "/Service/Logout");
            http.Method = "POST";
            http.ContentType = "application/x-www-form-urlencoded";

            CookieContainer cookies = new CookieContainer();
            Cookie sesja = new Cookie("ASP.NET_SessionId", this.session);
            sesja.Domain = cookieUrl;
            Cookie cAuth = new Cookie(".ASPXAUTH", auth);
            cAuth.Domain = cookieUrl;
            cookies.Add(cAuth);
            cookies.Add(sesja);
            http.CookieContainer = cookies;

            using (var writer = new StreamWriter(http.GetRequestStream()))
            {
            }
            var response = http.GetResponse();
            StreamReader loResponseStream = new StreamReader(response.GetResponseStream());
            string responsebody = loResponseStream.ReadToEnd();

        }

        public UserDataResponse getUserData(int id) {
            var http = (HttpWebRequest)WebRequest.Create(homeUrl + "/Service/GetUserDataById");
            http.Method = "POST";
            http.ContentType = "application/x-www-form-urlencoded";

            CookieContainer cookies = new CookieContainer();
            Cookie sesja = new Cookie("ASP.NET_SessionId", this.session);
            sesja.Domain = cookieUrl;
            Cookie cAuth = new Cookie(".ASPXAUTH", auth);
            cAuth.Domain = cookieUrl;
            cookies.Add(cAuth);
            cookies.Add(sesja);
            http.CookieContainer = cookies;

            using (var writer = new StreamWriter(http.GetRequestStream()))
            {
                writer.Write("user_id=" + id);
            }
            var response = http.GetResponse();
            StreamReader loResponseStream = new StreamReader(response.GetResponseStream());
            string responsebody = loResponseStream.ReadToEnd();
            JavaScriptSerializer json_serializer = new JavaScriptSerializer();
            UserDataResponse userDataResponse = json_serializer.Deserialize<UserDataResponse>(responsebody);
            return userDataResponse;
        }

        public List<GetContactListResult> getContactList()
        {
            var http = (HttpWebRequest)WebRequest.Create(homeUrl + "/Service/GetContactList");
            http.Method = "POST";
            http.ContentType = "application/x-www-form-urlencoded";

            CookieContainer cookies = new CookieContainer();
            Cookie sesja = new Cookie("ASP.NET_SessionId", this.session);
            sesja.Domain = cookieUrl;
            Cookie cAuth = new Cookie(".ASPXAUTH", auth);
            cAuth.Domain = cookieUrl;
            cookies.Add(cAuth);
            cookies.Add(sesja);
            http.CookieContainer = cookies;


            using (var writer = new StreamWriter(http.GetRequestStream()))
            {
            }
            try {
                
                var response = http.GetResponse();
                StreamReader loResponseStream = new StreamReader(response.GetResponseStream());
                string responsebody = loResponseStream.ReadToEnd();
                JavaScriptSerializer json_serializer = new JavaScriptSerializer();
                List<GetContactListResult> getContactListResult = json_serializer.Deserialize<List<GetContactListResult>>(responsebody);

                return getContactListResult;
            }catch(Exception e){
                return null;
            }

            
        }

        public bool setStatus(int status, string description)
        {
            var http = (HttpWebRequest)WebRequest.Create(homeUrl + "/Service/SetStatus");
            http.Method = "POST";
            http.ContentType = "application/x-www-form-urlencoded";

            CookieContainer cookies = new CookieContainer();
            Cookie sesja = new Cookie("ASP.NET_SessionId", this.session);
            sesja.Domain = cookieUrl;
            Cookie cAuth = new Cookie(".ASPXAUTH", auth);
            cAuth.Domain = cookieUrl;
            cookies.Add(cAuth);
            cookies.Add(sesja);
            http.CookieContainer = cookies;

            using (var writer = new StreamWriter(http.GetRequestStream()))
            {
                writer.Write("status=" + status.ToString());
                writer.Write("&description=" + description);
            }
            var response = http.GetResponse();
            StreamReader loResponseStream = new StreamReader(response.GetResponseStream());
            string responsebody = loResponseStream.ReadToEnd();
            return true;
        }

        public DateTime sendMessage(int me,List<int> users, string message)
        {
            var http = (HttpWebRequest)WebRequest.Create(homeUrl + "/Service/SendMessage");
            http.Method = "POST";
            http.ContentType = "application/x-www-form-urlencoded";

            CookieContainer cookies = new CookieContainer();
            Cookie sesja = new Cookie("ASP.NET_SessionId", this.session);
            sesja.Domain = cookieUrl;
            Cookie cAuth = new Cookie(".ASPXAUTH", auth);
            cAuth.Domain = cookieUrl;
            cookies.Add(cAuth);
            cookies.Add(sesja);
            http.CookieContainer = cookies;

            String userList = "";
            for (int i = 0; i < users.Count(); i++) {
                userList = userList + "&users=" + users[i];             
            }
            using (var writer = new StreamWriter(http.GetRequestStream()))
            {
                writer.Write("users="+me+userList);
                writer.Write("&message=" + message);
            }
            var response = http.GetResponse();
            StreamReader loResponseStream = new StreamReader(response.GetResponseStream());
            string responsebody = loResponseStream.ReadToEnd();
            JavaScriptSerializer json_serializer = new JavaScriptSerializer();
            SendMessangeResponse sendMessangeResponse = json_serializer.Deserialize<SendMessangeResponse>(responsebody);
            DateTime origin = new DateTime(1970, 1, 1, 0, 0, 0, 0);
            origin = origin.AddMilliseconds(sendMessangeResponse.timestamp);
            return origin;

        }

        public List<GetMessageResponse> getMessange()
        {
            var http = (HttpWebRequest)WebRequest.Create(homeUrl + "/Service/GetMessage");
            http.Method = "POST";
            http.ContentType = "application/x-www-form-urlencoded";

            CookieContainer cookies = new CookieContainer();
            Cookie sesja = new Cookie("ASP.NET_SessionId", this.session);
            //sesja.Domain = "localhost";
            sesja.Domain = cookieUrl;
            Cookie cAuth = new Cookie(".ASPXAUTH", auth);
            cAuth.Domain = cookieUrl;
            cookies.Add(cAuth);
            cookies.Add(sesja);
            http.CookieContainer = cookies;

            using (var writer = new StreamWriter(http.GetRequestStream()))
            {
            }

            RequestState rs = new RequestState();
            rs.Request = http;

            // Issue the async request.
            IAsyncResult r = (IAsyncResult)http.BeginGetResponse(
               new AsyncCallback(RespCallback), rs);
            //allDone.WaitOne();
            String responsebody = rs.RequestData.ToString();
            JavaScriptSerializer json_serializer = new JavaScriptSerializer();
            List<GetMessageResponse> getMessange= json_serializer.Deserialize<List<GetMessageResponse>>(responsebody);
            if (getMessange == null)
            {
                return null;
            }
            else {
                return getMessange;
            }
            
        }

        public List<GetStatusUpdateResponse_User> getStatusUpdate()
        {
            var http = (HttpWebRequest)WebRequest.Create(homeUrl + "/Service/GetStatusUpdate");
            http.Method = "POST";
            http.ContentType = "application/x-www-form-urlencoded";

            CookieContainer cookies = new CookieContainer();
            Cookie sesja = new Cookie("ASP.NET_SessionId", this.session);
            sesja.Domain = cookieUrl;
            Cookie cAuth = new Cookie(".ASPXAUTH", auth);
            cAuth.Domain = cookieUrl;
            cookies.Add(cAuth);
            cookies.Add(sesja);
            http.CookieContainer = cookies;

            using (var writer = new StreamWriter(http.GetRequestStream()))
            {
            }

            RequestState rs = new RequestState();
            rs.Request = http;

            // Issue the async request.
            IAsyncResult r = (IAsyncResult)http.BeginGetResponse(
               new AsyncCallback(RespCallback), rs);
            String responsebody = rs.RequestData.ToString();
            JavaScriptSerializer json_serializer = new JavaScriptSerializer();
            List<GetStatusUpdateResponse_User> getResponse = json_serializer.Deserialize<List<GetStatusUpdateResponse_User>>(responsebody);
            return getResponse;
        }

        private static void RespCallback(IAsyncResult ar)
        {
            try
            {
                RequestState rs = (RequestState)ar.AsyncState;
                WebRequest req = rs.Request;
                WebResponse resp = req.EndGetResponse(ar);
                Stream ResponseStream = resp.GetResponseStream();
                rs.ResponseStream = ResponseStream;
                IAsyncResult iarRead = ResponseStream.BeginRead(rs.BufferRead, 0, BUFFER_SIZE, new AsyncCallback(ReadCallBack), rs);
            }
            catch (Exception e)
            {

            }
        }


        private static void ReadCallBack(IAsyncResult asyncResult)
        {
            RequestState rs = (RequestState)asyncResult.AsyncState;
 
            Stream responseStream = rs.ResponseStream;

            int read = responseStream.EndRead(asyncResult);
            if (read > 0)
            {
                Char[] charBuffer = new Char[BUFFER_SIZE];

                int len =
                   rs.StreamDecode.GetChars(rs.BufferRead, 0, read, charBuffer, 0);

                String str = new String(charBuffer, 0, len);

                rs.RequestData.Append(
                   Encoding.ASCII.GetString(rs.BufferRead, 0, read));

                IAsyncResult ar = responseStream.BeginRead(
                   rs.BufferRead, 0, BUFFER_SIZE,
                   new AsyncCallback(ReadCallBack), rs);
            }
            else
            {
                if (rs.RequestData.Length > 0)
                {
                    string strContent;
                    strContent = rs.RequestData.ToString();
                }
                responseStream.Close();
                allDone.Set();
            }
            return;
        } 

    }
}
