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
