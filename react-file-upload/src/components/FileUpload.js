import React, { useState, useEffect } from "react";
import { uploadFiles, getFiles } from "../api";

const FileUpload = () => {
    const [selectedFiles, setSelectedFiles] = useState([]);
    const [message, setMessage] = useState("");
    const [files, setFiles] = useState([]);

    useEffect(() => {
        fetchFiles();
    }, []);

    const fetchFiles = () => {
        getFiles()
            .then((res) => setFiles(res.data))
            .catch(() => setFiles([]));
    };

    const handleFileChange = (e) => {
        setSelectedFiles(Array.from(e.target.files));
    };

    const handleUpload = async (e) => {
        e.preventDefault();
        if (!selectedFiles.length) return;

        try {
            await uploadFiles(selectedFiles);
            setMessage("Files uploaded successfully!");
            setSelectedFiles([]);
            fetchFiles();
        } catch (error) {
            setMessage("File upload failed!");
        }
    };

    return (
        <div style={{ maxWidth: 600, margin: "40px auto", padding: 24, border: "1px solid #ccc", borderRadius: 8 }}>
            <h2>Multiple File Upload</h2>
            <form onSubmit={handleUpload}>
                <input type="file" multiple onChange={handleFileChange} />
                <button type="submit" style={{ marginLeft: 8 }}>Upload</button>
            </form>
            {message && <p>{message}</p>}
            <h3>Uploaded Files</h3>
            <ul>
                {files.map((file) => (
                    <li key={file.name}>
                        <a href={file.url} target="_blank" rel="noopener noreferrer">{file.name}</a>
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default FileUpload;