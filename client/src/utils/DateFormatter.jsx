export const dateFormatter = (dateString) => {
    const date = new Date(dateString);

    const day = date.getDate();
    const year = date.getFullYear();

    const monthNames = [
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    ];
    const month = monthNames[date.getMonth()];


    let suffix;
    if (day > 3 && day < 21) {
        suffix = "th";
    } else {
        switch (day % 10) {
            case 1:  suffix = "st"; break;
            case 2:  suffix = "nd"; break;
            case 3:  suffix = "rd"; break;
            default: suffix = "th"; break;
        }
    }

    return `${day}${suffix} ${month} ${year}`;
};