package com.example.discovermada.model;

public class SignupResponse {
    private User response;
    private int error;
    private String errorMessage;

    public User getResponse() {
        return response;
    }

    public int getError() {
        return error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setResponse(User response) {
        this.response = response;
    }

    public void setError(int error) {
        this.error = error;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public class User {
        private String _id;
        private String username;
        private String email;
        private String password;
        private String created_at;
        private int __v;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public int get__v() {
            return __v;
        }

        public void set__v(int __v) {
            this.__v = __v;
        }
    }
}

