namespace duta.Interface
{
    public class LoginResponse
    {
        public LoginResponse(bool status)
        {
            logged_in = status ? 1 : 0;
        }

        public int logged_in { get; set; }
    }
}