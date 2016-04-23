var spqrqlQuery1 = 'PREFIX HAIOnt: <http://cbakerlab.unbsj.ca:8080/haitohdemo/HAI.owl#>\n'

+'PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n\n'

+ 'SELECT distinct ?patient ?diagnosis_code\n' + 'FROM <http://cbakerlab.unbsj.ca:8080/haitohdemo/getDiagnosisIDByPatientID_input.rdf>\n' + 'WHERE{\n' + '	?patient rdf:type HAIOnt:Patient .\n' + '	?patient HAIOnt:has_diagnosis ?diagnosis .\n' + '	?diagnosis HAIOnt:has_diagnosis_code ?diagnosis_code .	\n' + '}';