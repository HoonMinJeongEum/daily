import { sleep } from 'k6';
import { Trend, Rate } from 'k6/metrics';
import { loginGetToken, selectProfile } from './auth.js';
import { addCoupon, deleteCoupon, buyCoupon, useCoupon, getCoupons, getCouponsChild, getCouponsUser, insertCoupons, insertAndBuyCoupons } from './apis.js';

export const options = {
  vus: Number(__ENV.VUS),
  duration: '1m',
  setupTimeout: '5m',
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<500'],  
  },
};

const targetTrend = new Trend('target_duration');
const targetOK    = new Rate('target_ok');

export function setup() {
  let token = loginGetToken();
  let profileToken = selectProfile(token, 1);
  const profileTokens = [];

  for (let i = 1; i <= __ENV.VUS; i++) {
    const pt = selectProfile(token, Number(i));
    profileTokens.push(pt);
  }

  const couponIds = insertCouponSetup(profileToken);

  return {profileTokens, couponIds};
}

export default function (data) {
  const {profileTokens, couponIds} = data;


  const total = couponIds.length;
  const vus   = Number(__ENV.VUS);
  const slice = Math.floor(total / vus) || 1;
  const start = ( __VU - 1 ) * slice;
  const end   = __VU === vus ? total : start + slice;
  
  if (start >= total) return; 

  const idxInSlice = start + (__ITER % Math.max(1, end - start));
  const id = couponIds[idxInSlice];
  const idx = (__VU - 1) % __ENV.VUS;
  const profileToken = profileTokens[idx];

  const res = buyCoupon(profileToken, { couponId: id });

  targetOK.add(res.status >= 200 && res.status < 300);
  targetTrend.add(res.timings.duration);

  sleep(0.2);
}

// export function setup() {
//   let token = loginGetToken();
//   let profileToken = selectProfile(token, 1);
//   return { token, profileToken };
  
//   // const couponIds = insertCouponSetup(token);
//   // const couponIds = insertAndBuyCouponSetup(profileToken);
//   // return { token, profileToken, couponIds };
// }

// // -------- default (파일 기반 단일 엔드포인트 부하) --------
// export default function (data) {
//   const token = data.token;
//   const profileToken = data.profileToken;

//   const res = getCoupons(profileToken);
//   // const res = getCouponsUser(profileToken);
//   // const res = getCouponsChild(token);
//   // const res = addCoupon(profileToken, {description: 'test', price: 1});

//   // const couponIds = data.couponIds;
//   // const total = couponIds.length;
//   // const vus   = Number(__ENV.VUS);
//   // const slice = Math.floor(total / vus) || 1;
//   // const start = ( __VU - 1 ) * slice;
//   // const end   = __VU === vus ? total : start + slice;
  
//   // if (start >= total) return; 

//   // const idxInSlice = start + (__ITER % Math.max(1, end - start));
//   // const id = couponIds[idxInSlice];

//   // const res = deleteCoupon(token, id);
  
//   // const res = useCoupon(profileToken, {earnedCouponId: id});

//   targetOK.add(res.status >= 200 && res.status < 300);
//   targetTrend.add(res.timings.duration);

//   sleep(0.2);
// }

function insertCouponSetup(token) {
  const res = insertCoupons(token);
  return res.json();
}

function insertAndBuyCouponSetup(profileToken) {
  const res = insertAndBuyCoupons(profileToken);
  return res.json();
}
