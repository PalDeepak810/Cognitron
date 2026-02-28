package com.proc.proc.Service;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class SkillExtractor {

    private static final Set<String> SKILL_KEYWORDS = new HashSet<>(Arrays.asList(
        // Programming Languages
        "Java", "Python", "JavaScript", "TypeScript", "C++", "C#", "Go", "Rust", "Ruby", "PHP", "Kotlin", "Swift", "Scala",
        
        // Web Technologies
        "React", "Angular", "Vue", "Node.js", "Express", "Spring Boot", "Django", "Flask", "HTML", "CSS", "REST API", "GraphQL",
        
        // Databases
        "MySQL", "PostgreSQL", "MongoDB", "Redis", "Oracle", "SQL Server", "Cassandra", "DynamoDB",
        
        // Cloud & DevOps
        "AWS", "Azure", "GCP", "Docker", "Kubernetes", "Jenkins", "CI/CD", "Terraform", "Ansible",
        
        // Tools & Frameworks
        "Git", "Linux", "Microservices", "Agile", "Scrum", "Jira", "Maven", "Gradle",
        
        // Data & AI
        "Machine Learning", "Deep Learning", "TensorFlow", "PyTorch", "Pandas", "NumPy", "Spark", "Hadoop"
    ));

    public String extractSkills(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }

        Set<String> foundSkills = new LinkedHashSet<>();
        
        for (String skill : SKILL_KEYWORDS) {
            Pattern pattern = Pattern.compile("\\b" + Pattern.quote(skill) + "\\b", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(text);
            
            if (matcher.find()) {
                foundSkills.add(skill);
            }
        }

        return foundSkills.isEmpty() ? null : String.join(", ", foundSkills);
    }
}
