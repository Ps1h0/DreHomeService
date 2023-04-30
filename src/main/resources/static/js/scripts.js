function goToDevices() {
    axios({
        method: 'get',
        url: '/get_devices'
    })
}
function getDevices() {
    setInterval(() => {
        axios({
            method: 'get',
            url: '/api/devices'
        }).then(response => {
            let rows = response.data.length;
            let cells = 4;
            let elem = document.querySelector('#elem');

            createTable(elem, cells, rows, response.data);
        })
    }, 500)
}

function createTable(parent, cols, rows, data) {
    clearTable();

    let table = document.getElementById('devices');
    let tr = document.createElement('tr');
    let td = document.createElement('td');

    let th = document.createElement('th');
    th.innerHTML = 'Идентификатор';
    tr.appendChild(th);

    th = document.createElement('th');
    th.innerHTML = 'Имя устройства';
    tr.appendChild(th);

    th = document.createElement('th');
    th.innerHTML = 'Статус устройства';
    tr.appendChild(th);

    th = document.createElement('th');
    th.innerHTML = 'Тип устройства';
    tr.appendChild(th);
    table.appendChild(tr);

    for (let i = 0; i < data.length; i++) {
        tr = document.createElement('tr');
        td = document.createElement('td');
        td.innerHTML = data[i].devId;
        tr.appendChild(td);
        td = document.createElement('td');
        td.innerHTML = data[i].devName;
        tr.appendChild(td);
        td = document.createElement('td');
        if (data[i].included) {
            td.innerHTML = 'Включено';
        } else {
            td.innerHTML = 'Выключено';
        }

        tr.appendChild(td);
        td = document.createElement('td');
        td.innerHTML = data[i].type;
        tr.appendChild(td);
        table.appendChild(tr)
    }
}

function clearTable() {
    let table = document.getElementById('devices');
    const rows = table.getElementsByTagName('tr').length;
    for (let i = rows - 1; i >= 0; i--) {
        table.deleteRow(i);
    }
}

function switchDevice() {
    let deviceId = document.getElementById('deviceId').value;
    console.log("Ид устройства: " + deviceId);
    axios({
        method: 'get',
        url: '/api/switch',
        params: {
            id: deviceId
        }
    }).then(response => {
        let rows = response.data.length;
        let cells = 4;
        let elem = document.querySelector('#elem');

        createTable(elem, cells, rows, response.data);
    })
}

async function subscribe(id) {
    let response = await fetch('/api/longPolling?id=' + id);
    if (response.status == 502) {
        await subscribe();
    } else if (response.status != 200) {
        await new Promise(resolve => setTimeout(resolve, 1000));
        await subscribe();
    } else {
        await subscribe();
    }
}