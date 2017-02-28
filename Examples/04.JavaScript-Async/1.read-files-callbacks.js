let fs = require('fs');

function fetchStudent (studentId, callback) {
    fs.readFile('./data/student.json', function (err, data) {
        if (err) {
            callback(err);
            return;
        }

        let students = JSON.parse(data);
        students = students.filter(s => s.studentId === studentId);
        if (students.length > 0) {
            delete students[0].password;
            callback(null, students[0]);
        }
        else {
            callback(new Error("No records found"));
        }
    });
}

function fetchCourses (courseIds, callback) {
    fs.readFile('./data/course.json', function (err, data) {
        if (err) {
            callback(err);
            return;
        }

        let courses = JSON.parse(data);
        courses = courses.filter(c => courseIds.indexOf(c.crn) >= 0);
        //console.log(courses);
        callback(null, courses);
    });
}

function getStudent(studentId, callback) {
    let student;

    fetchStudent(studentId, function (err, aStudent) {
        student = aStudent;
        fetchCourses(student.courseIds, function (err, courses) {
            student.courses = courses;
            callback(null, student);
        });
    });
}

let studentId = 2015001;
fetchStudent(studentId, function(err, student) {
    //Displays a pretty-printed multiline JSON representation indented with 2 spaces
    console.log(JSON.stringify(student, null, 2));
});

getStudent(studentId, function(err, student) {
    console.log("\nStudent with course details: ");
    console.log(JSON.stringify(student, null, 2));
});