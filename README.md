# E-HealthCareAssistanceApp

Description:

This is an android application that aims to provide instant medical assistance after identifying disease based on user selected symptoms.
The basic objective of this project is to implement an “automated disease diagnosis healthcare system” using fuzzy logic and some artificial intelligence.The system maintains a knowledge base through which it identifies the disease based on the percepts it gets. The system provides authentic and trustworthy advice.
The project works behind the idea of self-medication before consulting a doctor.

Working of Algorithm:

The algorithm works on the basis of symptoms frequency of occurence in different diseases.
1- First it checks all the possible symptoms stored and sort them according to their number of occurences in different diseases.
2- The sorting is placed in descending order that results in the most common symptoms to be placed on start and then symptoms with lower occurrences.
3- It then shows the top most (4) symptoms and a "none of these option"
4(a)- If the user selects a symptom it then retrieve all the diseases in which the selected symptom occurrs and then retrieve all the symptoms of that retrieved diseases. Then it again sorts the retrieved symptoms(of retrieved diseases) according to their occurences in those diseases, and repeats the process until only 1 disease is retrived.
The diseases are retrieved with the selected symptoms i.e if a symptom is selceted then the diseases of that symptom are retrieved and then their symptoms are sorted and the top 4 symptoms are shown. Now when the user selects the symptom, those diseases are retrieved in which the currently selected symptom and previously selected symptoms are present.
4(b)- If the user selects "none of these", then the symptoms from 5 to 8 indexes will shown accordingly (first top 4 symptoms were shown were shown).

After the disease is identified the cure of the disease is shown accordingly.
