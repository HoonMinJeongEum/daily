import http from 'k6/http';
import { check } from 'k6';
import { jsonHeaders } from './auth.js';

const BASE = 'http://app:8080/api';

export function addCoupon(token, body) {
  const res = http.post(`${BASE}/coupons`, JSON.stringify(body), { headers: jsonHeaders(token) });
  check(res, { 'add 2xx': r => r.status >= 200 && r.status < 300 });
  return res;
}

export function deleteCoupon(token, couponId) {
  const res = http.del(`${BASE}/coupons/${couponId}`, null, { headers: jsonHeaders(token) });
  check(res, { 'delete 204/2xx': r => r.status === 204 || (r.status >= 200 && r.status < 300) });
  return res;
}

export function buyCoupon(profileToken, body) {
  const res = http.post(`${BASE}/coupons/buy`, JSON.stringify(body), { headers: jsonHeaders(profileToken) });
  check(res, { 'buy 2xx': r => r.status >= 200 && r.status < 300 });
  return res;
}

export function useCoupon(profileToken, body) {
  const res = http.patch(`${BASE}/coupons/use`, JSON.stringify(body), { headers: jsonHeaders(profileToken) });
  check(res, { 'use 204/2xx': r => r.status === 204 || (r.status >= 200 && r.status < 300) });
  return res;
}

export function getCoupons(profileToken) {
  const res = http.get(`${BASE}/coupons`, { headers: jsonHeaders(profileToken) });
  check(res, { 'get_all 2xx': r => r.status >= 200 && r.status < 300 });
  return res;
}

export function getCouponsChild(token) {
  const res = http.get(`${BASE}/coupons/child`, { headers: jsonHeaders(token) });
  check(res, { 'get_child 2xx': r => r.status >= 200 && r.status < 300 });
  return res;
}

export function getCouponsUser(profileToken) {
  const res = http.get(`${BASE}/coupons/user`, { headers: jsonHeaders(profileToken) });
  check(res, { 'get_user 2xx': r => r.status >= 200 && r.status < 300 });
  return res;
}

export function insertCoupons(token) {
  const res = http.get(`${BASE}/test`, { headers: jsonHeaders(token) });
  check(res, { 'insert 2xx': r => r.status >= 200 && r.status < 300 });
  return res;
}

export function insertAndBuyCoupons(token) {
  const res = http.get(`${BASE}/test/insertAndBuy`, { headers: jsonHeaders(token) });
  check(res, { 'insert 2xx': r => r.status >= 200 && r.status < 300 });
  return res;
}
