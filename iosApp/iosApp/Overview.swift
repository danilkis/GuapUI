import Foundation

// Assume the Kotlin functions are available in Swift as `fetchGroups`, `fetchWeekInfo`, and `fetchLessons`
// Use the Kotlin functions from your project.

struct Overview: View {
    @State private var lessons: [Day] = []
    @State private var groups: [Group] = []
    @State private var selectedGroupId: Int = 0
    @State private var selectedWeekType: String = "Авто"
    @State private var isLoading: Bool = false

    private let days = ["Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье", "Авто"]
    private let weekTypes = ["Числитель", "Знаменатель", "Авто"]

    var body: some View {
        VStack {
            if isLoading {
                ProgressView()
                    .progressViewStyle(CircularProgressViewStyle())
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
            } else {
                HStack {
                    ExpandableGroupField(items: groups, label: "Группа", selectedGroupId: $selectedGroupId)
                    ExpandableWeekField(items: weekTypes, label: "Тип недели", selectedWeekType: $selectedWeekType)
                }
                ScrollView {
                    LazyVGrid(columns: [GridItem(.adaptive(minimum: 400))]) {
                        ForEach(lessons) { day in
                            DayCard(lessons: day.lessons, label: day.dayName)
                        }
                    }
                    .animation(.easeInOut, value: lessons)
                }
            }
        }
        .onAppear {
            loadGroups()
            loadWeekInfo()
        }
        .onChange(of: selectedGroupId) { _ in
            loadLessons()
        }
        .onChange(of: selectedWeekType) { _ in
            loadLessons()
        }
    }

    private func loadGroups() {
        isLoading = true
        do {
            groups = try fetchGroups()
        } catch {
            print("Error fetching groups: \(error)")
        }
        isLoading = false
    }

    private func loadWeekInfo() {
        isLoading = true
        do {
            let weekInfo = try fetchWeekInfo()
            // Do something with weekInfo if needed
        } catch {
            print("Error fetching week info: \(error)")
        }
        isLoading = false
    }

    private func loadLessons() {
        isLoading = true
        let weekNumber: Int
        switch selectedWeekType {
        case "Числитель":
            weekNumber = 1
        case "Знаменатель":
            weekNumber = 2
        case "Авто":
            // Assuming weekInfo is fetched and available
            weekNumber = (weekInfo?.isWeekOdd ?? true) ? 1 : 2
        default:
            weekNumber = 1
        }
        do {
            lessons = try fetchLessons(groupId: selectedGroupId, weekNumber: weekNumber)
        } catch {
            print("Error fetching lessons: \(error)")
        }
        isLoading = false
    }
}