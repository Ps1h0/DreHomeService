function createTableBody() {
    let rows = 3;
    let cells = 10;

    let table = document.getElementById('devices');
    table.innerHTML = ("<tr>" + "<td></td>".repeat(cells) + "</tr>").repeat(rows);
    tableFill();
}

function tableFill() {
    let fillFrom = 27;
    let td = document.querySelectorAll('#devices td');

    for (let i = 0; i < td.length; i++) {
        td[i].textContent = fillFrom--;
    }
}

function getDevices() {
    axios({
        method: 'get',
        url: '/api/devices'
    }).then(response => {
        let rows = response.data.length;
        let cells = 4;
        let elem = document.querySelector('#elem');

        createTable(elem, cells, rows, response.data);
    })
}

function createTable(parent, cols, rows, data) {
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
    table.appendChild(tr)

    for (let i = 0; i < data.length; i++) {
        tr = document.createElement('tr');
        td = document.createElement('td');
        td.innerHTML = data[i].devId;
        tr.appendChild(td);
        td = document.createElement('td');
        td.innerHTML = data[i].devName;
        tr.appendChild(td);
        td = document.createElement('td');
        td.innerHTML = data[i].isIncluded;
        tr.appendChild(td);
        td = document.createElement('td');
        td.innerHTML = data[i].type;
        tr.appendChild(td);
        table.appendChild(tr)
    }
}
