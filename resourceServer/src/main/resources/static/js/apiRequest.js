export async function fetchData(endpoint, method, body) {
    const AUTH_SERVER_URL = "http://localhost:9000"
    let res;
    let options;

    if (method === "GET" || method === "DELETE") {
        options = {
            method: method,
            credentials: "include"
        };
    } else if (method === "POST" || method === "PUT" || method === "PATCH") {
        options = {
            method: method,
            credentials: "include",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(body)
        };
    } else {
        alert("알 수 없는 요청 메서드입니다.");
        return;
    }

    //1번째 요청
    res = await fetch(endpoint, options);

    //access 만료시 refresh
    if (res.status === 401) {
        const refresh = await fetch(AUTH_SERVER_URL+"/token/refresh", {
            method: "POST",
            credentials: "include"
        });

        if (!refresh.ok) {
            alert("쿠키가 만료되었습니다. 다시 로그인해주세요.");
            window.location.href = AUTH_SERVER_URL+"/login";
        }

        // 2번째 요청
        res = await fetch(endpoint, options);
    }

    //미인증 사용자
    if (res.status === 401) {
        alert("인증된 사용자만 접근할 수 있습니다.");
        window.location.href = AUTH_SERVER_URL+"/login";
    }

    //권한 부족
    if (res.status === 403) {
        alert("권한이 부족합니다.");
        window.history.back();
    }

    //기타 오류
    if (!res.ok) {
        alert("알 수 없는 오류가 발생했습니다.");
        window.history.back();
    }

    return await res.json();
}
