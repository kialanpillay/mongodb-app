const prizes = require("../data/prize.json").prizes;
const laureates = require("../data/laureate.json").laureates;
const fs = require("fs");
const path = require("path");

let laureate_obj = Object();
for (let l of laureates) {
  laureate_obj[l.id] = l;
  l.affiliations = [];
  for (let p of l.prizes) {
    l.affiliations = l.affiliations.concat(p.affiliations);
    delete p.affiliations;
  }
  delete l.id;
}

fs.writeFileSync(
  path.resolve(__dirname, "../data/output/laureate_documents.json"),
  JSON.stringify(laureates)
);

for (let p of prizes) {
  if (p.hasOwnProperty("laureates")) {
    for (let l of p.laureates) {
      Object.assign(l, l, laureate_obj[l.id]);
      delete l.prizes;
      delete l.id;
    }
  }
}

fs.writeFileSync(
  path.resolve(__dirname, "../data/output/prize_documents.json"),
  JSON.stringify(prizes)
);

let nobel_obj = Object();
nobel_obj["nobel_prizes"] = prizes;

fs.writeFileSync(
  path.resolve(__dirname, "../data/output/nobel.json"),
  JSON.stringify(nobel_obj)
);
