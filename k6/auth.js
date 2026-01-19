import http from "k6/http";
import { check, fail } from "k6";

const BASE = "http://app:8080/api";
const LOGIN_PATH = "/user/login";
const PROFILE_PATH = "/user/member";

const USERNAME = "test_user_1";
const PASSWORD = "1234";
const MEMBER_ID = Number(1);

export function jsonHeaders(token) {
  const h = { "Content-Type": "application/json" };
  if (token) h["Authorization"] = `Bearer ${token}`;
  return h;
}

function extractTokenFromHeaders(res) {
  const headers = res.headers || {};
  let token = null;

  const map = {};
  for (const k in headers) {
    map[k.toLowerCase()] = headers[k];
  }

  const candidates = ["authorization", "auth", "x-auth-token"];
  for (const key of candidates) {
    if (map[key]) {
      const v = Array.isArray(map[key]) ? map[key][0] : map[key];
      if (typeof v === "string" && v.toLowerCase().startsWith("bearer ")) {
        token = v.slice(7).trim();
      } else {
        token = v.trim();
      }
      break;
    }
  }
  return token;
}

export function loginGetToken(name) {
  const body = JSON.stringify({ username: name, password: PASSWORD });
  const res = http.post(`${BASE}${LOGIN_PATH}`, body, {
    headers: jsonHeaders(),
  });

  check(res, { "login 200": (r) => r.status === 200 }) ||
    fail(`Login failed: ${res.status} ${res.body}`);

  let token = extractTokenFromHeaders(res);

  if (!token) fail("No JWT token returned on login (header/body not found)");
  return token;
}

export function selectProfile(token, id) {
  const body = JSON.stringify({ memberId: id });
  const res = http.post(`${BASE}${PROFILE_PATH}`, body, {
    headers: jsonHeaders(token),
  });

  check(res, { "profile 2xx": (r) => r.status >= 200 && r.status < 300 }) ||
    fail(`Profile select failed: ${res.status} ${res.body}`);

  const newtoken = extractTokenFromHeaders(res);
  return newtoken;
}
