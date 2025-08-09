async function loadProtectedData(endpoint) {
    const AUTH_SERVER_URL = "http://localhost:9000"
    try {
        const res = await fetch(endpoint, {
            method: "GET",
            credentials: "include"
        });

        //access 실패
        if (res.status === 401) {
            const errorData = await res.json();

            if (errorData.error === "access_token_error") {
                const refreshRes = await fetch(AUTH_SERVER_URL + "/token/refresh", {
                    method: "POST",
                    credentials: "include"
                });
                //refresh 성공
                if (refreshRes.ok) {
                    const retryRes = await fetch(endpoint, {
                        method: "GET",
                        credentials: "include"
                    });
                    //access 재시도 성공
                    if (retryRes.ok) {
                        const data = await retryRes.text();
                        console.log("Data loaded:", data);
                    }
                    //access 재시도 실패
                    else {
                        window.location.href = AUTH_SERVER_URL + "/login";
                    }
                }
                //refresh 실패
                else {
                    window.location.href = AUTH_SERVER_URL + "/login";
                }

            } else if (errorData.error === "refresh_token_error") {
                window.location.href = AUTH_SERVER_URL + "/login";
            } else {
                console.error("Unauthorized access");
            }

        }
        //access 성공
        else if (res.ok) {
            const data = await res.text();
            console.log("Data loaded:", data);
        } else {
            console.error(`Request failed with status: ${res.status}`);
        }
    } catch (error) {
        console.error("Error during request:", error);
    }
}