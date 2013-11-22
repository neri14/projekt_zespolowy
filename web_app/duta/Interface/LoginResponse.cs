namespace duta.Interface
{
    public class LoginResponse
    {
        public LoginResponse(bool status, int user_id)
        {
            logged_in = status ? 1 : 0;
            this.user_id = user_id;
        }

        public int logged_in { get; set; }
        public int user_id { get; set; }
    }
}