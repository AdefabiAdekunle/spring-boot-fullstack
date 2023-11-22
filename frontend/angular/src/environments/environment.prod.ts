//prod is still using localhost because the elb link or the dns link is not yet register with route
// 53 and ssl certified to change it to https.
// elb http://adekunle-api-env-lb.us-east-2.elasticbeanstalk.com
// dns http://awseb--awseb-hyirr0ugfxsn-69307113.us-east-2.elb.amazonaws.com
export const environment = {
  api: {
    baseUrl: 'http://localhost:8080',
    authUrl: '/api/v1/auth/login',
    customerUrl: '/api/v1/customers'
  }
};
