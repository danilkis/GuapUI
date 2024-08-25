import Foundation
import SwiftUI

struct DayCard: View {
    let lessons: [Lesson]
    let label: String

    var body: some View {
        VStack(alignment: .leading) {
            Text(label)
                .font(.headline)
                .padding()
                .background(Color.blue)
                .foregroundColor(.white)
                .cornerRadius(8)
            
            ForEach(lessons) { lesson in
                LessonEntry(lesson: lesson)
            }
        }
        .padding()
        .background(Color.white)
        .cornerRadius(12)
        .shadow(radius: 4)
    }
}