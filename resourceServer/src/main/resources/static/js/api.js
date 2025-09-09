export async function getData(endpoint) {
    //GET
    let options = {
        method: "GET",
        credentials: "include"
    }

    return await sendRequest(endpoint, options);
}

export async function postData(endpoint, data) {
    //POST
    let options = {
        method: "POST",
        credentials: "include",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(data)
    }

    return await sendRequest(endpoint, options);
}

async function sendRequest(endpoint, options) {
    let AUTH_SERVER_URL = "http://localhost:9000"
    let res = await fetch(endpoint, options);

    // access 만료 시 refresh
    if (res.status === 401) {
        const refresh = await fetch(AUTH_SERVER_URL + "/token/refresh", {
            method: "POST",
            credentials: "include"
        });

        if (!refresh.ok) {
            alert("쿠키가 만료되었습니다. 다시 로그인해주세요.");
            window.location.href = AUTH_SERVER_URL + "/login";
            return;
        }

        // 2번째 요청
        res = await fetch(endpoint, options);
    }

    // 공통 에러 처리
    if (res.status === 400) {
        throw await res.json();
    } else if (res.status === 401) {
        alert("인증된 사용자만 접근할 수 있습니다.");
        window.location.href = AUTH_SERVER_URL + "/login";
    } else if (res.status === 403) {
        alert("권한이 부족합니다.");
        window.history.back();
    } else if (!res.ok) {
        alert("알 수 없는 오류가 발생했습니다.");
        window.history.back();
    }

    return await res.json();
}
