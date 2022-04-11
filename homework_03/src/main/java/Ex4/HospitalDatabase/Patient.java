package Ex4.HospitalDatabase;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "first_name", length = 50)
    private String firstname;

    @Column(name = "last_name", length = 50)
    private String lastName;


    private String address;

    private String email;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(columnDefinition = "MEDIUMBLOB")
    private byte[] image;

    @Column(name = "is_insured")
    private boolean isInsured;

    @OneToMany(targetEntity = Diagnose.class, mappedBy = "patient")
    private Set<Diagnose> diagnoses;

    @OneToMany(targetEntity = Medicament.class, mappedBy = "patient")
    private Set<Medicament> medicament;

    @OneToMany(targetEntity = Visitation.class, mappedBy = "patient")
    private Set<Visitation> visitations;

    public Patient() {
        visitations = new HashSet<>();
        medicament = new HashSet<>();
        diagnoses = new HashSet<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public boolean isInsured() {
        return isInsured;
    }

    public void setInsured(boolean insured) {
        isInsured = insured;
    }

    public Set<Diagnose> getDiagnoses() {
        return diagnoses;
    }

    public void setDiagnoses(Set<Diagnose> diagnoses) {
        this.diagnoses = diagnoses;
    }

    public Set<Medicament> getMedicament() {
        return medicament;
    }

    public void setMedicament(Set<Medicament> medicament) {
        this.medicament = medicament;
    }

    public Set<Visitation> getVisitations() {
        return visitations;
    }

    public void setVisitations(Set<Visitation> visitations) {
        this.visitations = visitations;
    }
}