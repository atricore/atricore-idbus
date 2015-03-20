using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using OAuth2Common;


namespace OAuth2Test
{
    class Program
    {
        static void Main(string[] args)
        {

            String token = "TZPHrqtWAAD/xVsi0Q1+0lvQizG92bsDHHqvhqf8e26UTbYzs50/N9in8zmu1dBzbXH7deMk7/bXbamKHqzbDP+DagdST+Xw/5sQtBv8cXeZvJe7YTl1BL9OHMt6szWcoUnm8PunX4cG9j/ZbnRoEPYacTzs10VtmISIMs6ATidV2o2OGbWLzOHA1t3Nu4cRR+dyLjozHIo5TlnaF37Fp2QsVz2ZlhSz5MsYRzs3KtmK7dcyZvbo+A52oY9h2mK8K1qlrxGsJlERogxDbisfFLNlt93h63lFlveRKQJSJWwj04FCto9+KM6sN/L7g+02rdVMTzGrsXk7ggfjZohfhOaQmD52xcxbD31tmmjT8kHvn4u+r34/8EmASF3yxOVq1Z5IGkbXOt93uTDTvVdhOZf2U/4EJZywRvN6Aja7UidCGjRiIsntxXYt2JgM81ZPO32+mq9nlaPdq0bewghwo1ZcZ5gNw8Uv2UURxaRdZ4NNBHhR3J+LtW07cHMhXCNDw2mqfoAjZCeZCcGSIXWa1FoG/YMwxW8oWFuEGEeyB8diau5Zlgc91Bqk+B6JD5kTQKBU8rEVCx9JDTUxCJJxIfbl+4rIEt5YHyLqCEebmRZqZNXcCDmvMmZK5OCVTbp5L8jrMPIXFx6mhdSINlyvWBEeFjLiOBRbALQR19nvLo68Hcp8P3BJ0QswnmjBefgMLT9V/N3k5+cz1O6X2gmiUwspWO8sSmtz7q0mIXxTES9SaKFKZbuuyLEDf4RSyatPiT3drfQhwVrp8uboINH6WpVmr0flBautbr6UZpKJiLRJYl7CUa3r/QzxxPdLXqjQ1fpg5KDzSOl+XfXiQEvKuWqAqqFPJXwP20RvEVfgSRiaY8XqQKWYeBZP3BWown9Rpljt37dyIHbkOlMufojMpTgjfUit/M4ND6moqvyWz+L3v4tkMG/BCrPbr3Xe4N//AA==";

            AESTokenEncrypter e = new AESTokenEncrypter();
            e.Key = "changeme";

            HMACTokenSigner s = new HMACTokenSigner();
            s.Key = "changeme";

            SecureAccessTokenResolver r = new SecureAccessTokenResolver();
            r.SetTokenEncrypter(e);
            r.SetTokenSigner(s);

            OAuth2AccessToken at = r.resolve(token);
            Console.WriteLine("USER:" + at.UserId);
            Console.WriteLine("LAST NAME:" + at.getAttribute("lastName"));

            for (int i = 0; i < at.Claims.Count ; i++ )
            {
                OAuth2Claim c = at.Claims.ElementAt(i);

                Console.WriteLine("TYPE :" + c.Type);
                Console.WriteLine("VALUE:" + c.Value);
                Console.WriteLine("ATTR :" + c.Attribute);
                Console.WriteLine("---------------------------");
            }

            int j = 0;

            j++;
        }
    }
}
