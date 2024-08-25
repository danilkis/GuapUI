import Foundation

import SwiftUI

struct LessonEntry: View {
    let lesson: Lesson

    var body: some View {
        VStack(alignment: .leading) {
            HStack {
                Text("\(lesson.number)")
                    .padding()
                    .background(Color.blue.opacity(0.2))
                    .cornerRadius(12)
                
                VStack(alignment: .leading) {
                    Text(lesson.time)
                        .font(.subheadline)
                    Text(lesson.lessonName)
                        .font(.title2)
                        .fontWeight(.semibold)
                    HStack {
                        Text(lesson.room)
                            .font(.subheadline)
                        Text(lesson.type)
                            .font(.subheadline)
                    }
                    
                    ForEach(lesson.teachers, id: \.self) { teacher in
                        HStack {
                            Image(systemName: "person.fill")
                            Text(teacher)
                        }
                        .padding(4)
                        .background(Color.gray.opacity(0.2))
                        .cornerRadius(8)
                    }

                    HStack {
                        Image(systemName: "house.fill")
                        Text(lesson.building)
                    }
                    .padding(4)
                    .background(Color.gray.opacity(0.2))
                    .cornerRadius(8)
                }
                .padding()
            }
        }
        .padding()
        .background(Color.white)
        .cornerRadius(8)
        .shadow(radius: 2)
    }
}