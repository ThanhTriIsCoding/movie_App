package com.example.ojt_aada_mockproject1_trint28.data.remote.model;

import java.util.List;

public class CastCrewResponse {
    private int id;
    private List<Cast> cast;
    private List<Crew> crew;

    public int getId() {
        return id;
    }

    public List<Cast> getCast() {
        return cast;
    }

    public List<Crew> getCrew() {
        return crew;
    }

    public static class Cast {
        private boolean adult;
        private int gender;
        private int id;
        private String known_for_department;
        private String name;
        private String original_name;
        private double popularity;
        private String profile_path;
        private int cast_id;
        private String character;
        private String credit_id;
        private int order;

        public boolean isAdult() {
            return adult;
        }

        public int getGender() {
            return gender;
        }

        public int getId() {
            return id;
        }

        public String getKnownForDepartment() {
            return known_for_department;
        }

        public String getName() {
            return name;
        }

        public String getOriginalName() {
            return original_name;
        }

        public double getPopularity() {
            return popularity;
        }

        public String getProfilePath() {
            return profile_path;
        }

        public int getCastId() {
            return cast_id;
        }

        public String getCharacter() {
            return character;
        }

        public String getCreditId() {
            return credit_id;
        }

        public int getOrder() {
            return order;
        }
    }

    public static class Crew {
        private boolean adult;
        private int gender;
        private int id;
        private String known_for_department;
        private String name;
        private String original_name;
        private double popularity;
        private String profile_path;
        private String credit_id;
        private String department;
        private String job;

        public boolean isAdult() {
            return adult;
        }

        public int getGender() {
            return gender;
        }

        public int getId() {
            return id;
        }

        public String getKnownForDepartment() {
            return known_for_department;
        }

        public String getName() {
            return name;
        }

        public String getOriginalName() {
            return original_name;
        }

        public double getPopularity() {
            return popularity;
        }

        public String getProfilePath() {
            return profile_path;
        }

        public String getCreditId() {
            return credit_id;
        }

        public String getDepartment() {
            return department;
        }

        public String getJob() {
            return job;
        }
    }
}